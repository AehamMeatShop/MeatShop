import http from 'k6/http';
import encoding from 'k6/encoding';
import { check } from 'k6';

export const BASE_URL = (__ENV.BASE_URL || 'https://172.16.65.137:444').replace(/\/$/, '');

export function newDevice(label) {
  return {
    did: `k6-${label}-${Date.now()}-${Math.floor(Math.random() * 1e9)}`,
    os: 'Linux',
    osVersion: '6.14',
    browser: 'k6',
    screenResolution: '1920x1080',
    token: null,
    refreshToken: null,
    sid: null,
    partyId: null,
  };
}

export function headers(session) {
  const h = {
    'Content-Type': 'application/json',
    did: session.did,
    os: session.os,
    osVersion: session.osVersion,
    browser: session.browser,
    screenResolution: session.screenResolution,
    'X-Real-IP': '172.16.65.10',
  };
  if (session.sid != null) {
    h.sid = String(session.sid);
  }
  if (session.token) {
    h.Authorization = `Bearer ${session.token}`;
  }
  return h;
}

export function decodeJwtPayload(token) {
  const part = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
  const pad = '='.repeat((4 - (part.length % 4)) % 4);
  return JSON.parse(encoding.b64decode(part + pad, 'std', 's'));
}

export function request(method, path, session, body, tagName) {
  const url = path.startsWith('http') ? path : `${BASE_URL}${path}`;
  const params = {
    headers: headers(session),
    tags: { name: tagName || `${method} ${path}` },
    timeout: '60s',
  };
  const payload = body === undefined || body === null ? null : JSON.stringify(body);
  return http.request(method, url, payload, params);
}

export function login(email, password, session) {
  const res = request('POST', '/auth/login', session, { email, password }, 'login');
  const ok = check(res, {
    'login status 202': (r) => r.status === 202,
  });
  if (!ok) {
    throw new Error(`login failed for ${email}: ${res.status} ${res.body}`);
  }
  const body = res.json();
  session.token = body.token;
  session.refreshToken = body.refreshToken;
  session.sid = body.sid;
  if (body.DID) {
    session.did = body.DID;
  } else if (body.did) {
    session.did = body.did;
  }
  session.partyId = Number(decodeJwtPayload(session.token).sub);
  session.email = email;
  return session;
}

export function jsonOrNull(res) {
  try {
    return res.json();
  } catch (e) {
    return null;
  }
}
