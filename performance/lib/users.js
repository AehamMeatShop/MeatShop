import { check } from 'k6';
import { login, newDevice, request, jsonOrNull } from './client.js';

export const USER_COUNT = Number(__ENV.USER_COUNT || 50);
export const USER_PASSWORD = __ENV.USER_PASSWORD || 'n8vR2kLm9pQw4sXz!';

const ROLE_PLAN = [
  { name: 'MANAGER', count: 10, authorities: [
    'PRODUCT_MANAGEMENT', 'CATEGORY_MANAGEMENT', 'STOCK_MANAGEMENT',
    'EMPLOYEE_MANAGEMENT', 'SELL_BUY_AUTHORITY', 'PARTY_MANAGEMENT',
    'VIEW_PRODUCTS', 'VIEW_CATEGORIES',
  ]},
  { name: 'CASHIER', count: 10, authorities: [
    'STOCK_MANAGEMENT', 'SELL_BUY_AUTHORITY', 'VIEW_PRODUCTS', 'VIEW_CATEGORIES',
  ]},
  { name: 'BUTCHER', count: 10, authorities: [
    'STOCK_MANAGEMENT', 'VIEW_PRODUCTS', 'VIEW_CATEGORIES',
  ]},
  { name: 'WORKER', count: 10, authorities: [
    'VIEW_PRODUCTS', 'VIEW_CATEGORIES',
  ]},
  { name: 'MANAGER', count: 10, authorities: [
    'PRODUCT_MANAGEMENT', 'CATEGORY_MANAGEMENT', 'STOCK_MANAGEMENT',
    'EMPLOYEE_MANAGEMENT', 'SELL_BUY_AUTHORITY', 'PARTY_MANAGEMENT',
    'VIEW_PRODUCTS', 'VIEW_CATEGORIES',
  ]},
];

export function k6Email(index) {
  const n = String(index).padStart(2, '0');
  return `k6.user.${n}@meatshop.test`;
}

function roleForIndex(index) {
  let cursor = 0;
  for (let i = 0; i < ROLE_PLAN.length; i++) {
    const next = cursor + ROLE_PLAN[i].count;
    if (index <= next) {
      return ROLE_PLAN[i];
    }
    cursor = next;
  }
  return ROLE_PLAN[0];
}

function listRoles(admin) {
  const res = request('GET', '/auth/roles', admin, null, 'list-roles');
  check(res, { 'list roles 200': (r) => r.status === 200 });
  return jsonOrNull(res) || [];
}

function ensureRole(admin, name) {
  const roles = listRoles(admin);
  for (let i = 0; i < roles.length; i++) {
    if (roles[i].name === name) {
      return roles[i];
    }
  }
  const created = request('POST', '/auth/roles', admin, { name }, 'create-role');
  if (created.status !== 201) {
    const again = listRoles(admin);
    for (let i = 0; i < again.length; i++) {
      if (again[i].name === name) {
        return again[i];
      }
    }
    throw new Error(`create role ${name} failed: ${created.status} ${created.body}`);
  }
  return created.json();
}

function authorityMap(admin, adminPartyId) {
  const res = request(
    'GET',
    `/auth/authorities/party?partyType=EMPLOYEE&partyId=${adminPartyId}`,
    admin,
    null,
    'list-authorities',
  );
  check(res, { 'list authorities 200': (r) => r.status === 200 });
  const list = jsonOrNull(res) || [];
  const map = {};
  for (let i = 0; i < list.length; i++) {
    map[list[i].authority] = list[i].id;
  }
  return map;
}

function attachAuthorities(admin, roleId, names, authByName) {
  for (let i = 0; i < names.length; i++) {
    const authId = authByName[names[i]];
    if (!authId) {
      console.warn(`authority ${names[i]} not found on admin; skip`);
      continue;
    }
    request(
      'POST',
      `/auth/roles/${roleId}/authorities/${authId}`,
      admin,
      null,
      'assign-authority-to-role',
    );
  }
}

export function ensureRoles(admin) {
  const authByName = authorityMap(admin, admin.partyId);
  const unique = {};
  for (let i = 0; i < ROLE_PLAN.length; i++) {
    unique[ROLE_PLAN[i].name] = ROLE_PLAN[i];
  }
  const roleIds = {};
  const keys = Object.keys(unique);
  for (let i = 0; i < keys.length; i++) {
    const plan = unique[keys[i]];
    const role = ensureRole(admin, plan.name);
    attachAuthorities(admin, role.id, plan.authorities, authByName);
    roleIds[plan.name] = role.id;
  }
  return roleIds;
}

function createEmployee(admin, email, index) {
  const res = request(
    'POST',
    '/employees',
    admin,
    {
      name: `K6 User ${index}`,
      address: 'k6 load street',
      email,
      password: USER_PASSWORD,
      salary: 1000,
      status: 'ACTIVE',
    },
    'create-employee',
  );
  return res;
}

export function ensureUsers(admin, roleIds) {
  const users = [];
  for (let i = 1; i <= USER_COUNT; i++) {
    const email = k6Email(i);
    const plan = roleForIndex(i);
    const created = createEmployee(admin, email, i);
    if (created.status !== 201 && created.status !== 200) {
      console.warn(`employee ${email} create status ${created.status}: ${created.body}`);
    }

    const session = newDevice(`user-${i}`);
    login(email, USER_PASSWORD, session);
    session.role = plan.name;
    session.canWriteStock =
      plan.name === 'MANAGER' || plan.name === 'CASHIER' || plan.name === 'BUTCHER';

    const assign = request(
      'POST',
      '/auth/roles/assign-to-party',
      admin,
      {
        partyType: 'EMPLOYEE',
        partyId: session.partyId,
        roleId: roleIds[plan.name],
      },
      'assign-role',
    );
    check(assign, {
      'assign role 201 or already': (r) => r.status === 201 || r.status === 400 || r.status === 409,
    });

    users.push({
      email: session.email,
      role: session.role,
      canWriteStock: session.canWriteStock,
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
  return users;
}
