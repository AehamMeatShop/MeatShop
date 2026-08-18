import { check, sleep } from 'k6';
import { login, newDevice, request } from './lib/client.js';
import { k6Email, USER_COUNT, USER_PASSWORD } from './lib/users.js';
import { sellBody, adjustmentBody } from './lib/catalog.js';

const VUS = Number(__ENV.VUS || 50);

export const options = {
  insecureSkipTLSVerify: true,
  scenarios: {
    mixedRoles: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '30s', target: VUS },
        { duration: '2m', target: VUS },
        { duration: '20s', target: 0 },
      ],
      gracefulRampDown: '10s',
      exec: 'stress',
    },
  },
  thresholds: {
    http_req_failed: ['rate<0.1'],
    http_req_duration: ['p(95)<2000'],
  },
};

function k6Products(admin) {
  const res = request('GET', '/products', admin, null, 'list-products');
  check(res, { 'list products 200': (r) => r.status === 200 });
  const all = res.json() || [];
  const ids = [];
  for (let i = 0; i < all.length; i++) {
    if (String(all[i].productName).indexOf('k6-') === 0) {
      ids.push(all[i].id);
    }
  }
  if (ids.length === 0) {
    throw new Error('No k6- products found. Run seed.js first.');
  }
  return ids;
}

export function setup() {
  const email = __ENV.ADMIN_EMAIL;
  const password = __ENV.ADMIN_PASSWORD;
  if (!email || !password) {
    throw new Error('Set ADMIN_EMAIL and ADMIN_PASSWORD');
  }

  const admin = login(email, password, newDevice('admin-stress'));
  const productIds = k6Products(admin);
  const users = [];

  for (let i = 1; i <= USER_COUNT; i++) {
    const session = login(k6Email(i), USER_PASSWORD, newDevice(`stress-${i}`));
    let role = 'WORKER';
    let canWriteStock = false;
    if (i <= 10 || i > 40) {
      role = 'MANAGER';
      canWriteStock = true;
    } else if (i <= 20) {
      role = 'CASHIER';
      canWriteStock = true;
    } else if (i <= 30) {
      role = 'BUTCHER';
      canWriteStock = true;
    }
    users.push({
      email: session.email,
      role,
      canWriteStock,
      did: session.did,
      os: session.os,
      osVersion: session.osVersion,
      browser: session.browser,
      screenResolution: session.screenResolution,
      token: session.token,
      refreshToken: session.refreshToken,
      sid: session.sid,
      partyId: session.partyId,
    });
  }

  return { productIds, users };
}

export function stress(data) {
  const user = data.users[(__VU - 1) % data.users.length];
  const productId = data.productIds[Math.floor(Math.random() * data.productIds.length)];
  const roll = Math.random();

  if (roll < 0.3) {
    const res = request('GET', '/products', user, null, 'get-products');
    check(res, { 'get products 200': (r) => r.status === 200 });
  } else if (roll < 0.5) {
    const res = request('GET', '/categories', user, null, 'get-categories');
    check(res, { 'get categories 200': (r) => r.status === 200 });
  } else if (roll < 0.7) {
    const res = request(
      'GET',
      `/stock-movements/product/${productId}/stock`,
      user,
      null,
      'get-stock',
    );
    check(res, { 'get stock 200': (r) => r.status === 200 });
  } else if (user.canWriteStock && roll < 0.9) {
    const body = Math.random() < 0.7 ? adjustmentBody(productId, `stress-${__VU}`) : sellBody(productId);
    const res = request('POST', '/stock-movements', user, body, 'post-stock');
    check(res, {
      'post stock 201 or 400': (r) => r.status === 201 || r.status === 400,
    });
  } else {
    const res = request(
      'POST',
      '/auth/refresh',
      user,
      { refreshToken: user.refreshToken, sessionId: user.sid },
      'refresh',
    );
    if (res.status === 202) {
      const body = res.json();
      user.token = body.token;
      user.refreshToken = body.refreshToken;
      user.sid = body.sid;
    }
  }

  sleep(0.3 + Math.random() * 0.5);
}
