import { check } from 'k6';
import { Counter } from 'k6/metrics';
import { login, newDevice, request } from './lib/client.js';
import { ensureCatalog, adjustmentBody } from './lib/catalog.js';
import { ensureRoles, ensureUsers } from './lib/users.js';

const MOVEMENTS = Number(__ENV.MOVEMENTS || 10000);
const SEED_VUS = Number(__ENV.SEED_VUS || 20);

const seededOk = new Counter('stock_movements_seeded');

export const options = {
  insecureSkipTLSVerify: true,
  scenarios: {
    seedStock: {
      executor: 'shared-iterations',
      vus: SEED_VUS,
      iterations: MOVEMENTS,
      maxDuration: '20m',
      exec: 'seedMovements',
    },
  },
  thresholds: {
    checks: ['rate>0.95'],
  },
};

export function setup() {
  const email = __ENV.ADMIN_EMAIL;
  const password = __ENV.ADMIN_PASSWORD;
  if (!email || !password) {
    throw new Error('Set ADMIN_EMAIL and ADMIN_PASSWORD');
  }

  const admin = login(email, password, newDevice('admin-seed'));
  const catalog = ensureCatalog(admin);
  const roleIds = ensureRoles(admin);
  const users = ensureUsers(admin, roleIds);

  console.log(
    `catalog ready: ${catalog.categoryIds.length} categories, ${catalog.products.length} products, ${users.length} users`,
  );

  return {
    admin,
    productIds: catalog.products.map((p) => p.id),
  };
}

export function seedMovements(data) {
  const productIds = data.productIds;
  const productId = productIds[(__ITER + __VU) % productIds.length];
  const res = request(
    'POST',
    '/stock-movements',
    data.admin,
    adjustmentBody(productId, `${__VU}-${__ITER}`),
    'seed-stock',
  );
  const ok = check(res, { 'seed movement 201': (r) => r.status === 201 });
  if (ok) {
    seededOk.add(1);
  }
}
