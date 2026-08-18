import { check } from 'k6';
import { request, jsonOrNull } from './client.js';

const CATEGORY_NAMES = ['k6-beef', 'k6-poultry', 'k6-lamb'];
const PRODUCT_SPECS = [
  { name: 'k6-ribeye', category: 0 },
  { name: 'k6-mince', category: 0 },
  { name: 'k6-brisket', category: 0 },
  { name: 'k6-chicken-breast', category: 1 },
  { name: 'k6-chicken-thigh', category: 1 },
  { name: 'k6-whole-chicken', category: 1 },
  { name: 'k6-lamb-leg', category: 2 },
  { name: 'k6-lamb-chops', category: 2 },
];

function findByName(list, name) {
  if (!list) {
    return null;
  }
  for (let i = 0; i < list.length; i++) {
    if (list[i].name === name || list[i].productName === name) {
      return list[i];
    }
  }
  return null;
}

export function ensureCatalog(admin) {
  const catsRes = request('GET', '/categories', admin, null, 'list-categories');
  check(catsRes, { 'list categories 200': (r) => r.status === 200 });
  const existingCats = jsonOrNull(catsRes) || [];

  const categoryIds = [];
  for (let i = 0; i < CATEGORY_NAMES.length; i++) {
    const name = CATEGORY_NAMES[i];
    const existing = findByName(existingCats, name);
    if (existing) {
      categoryIds.push(existing.id);
      continue;
    }
    const created = request('POST', '/categories', admin, { name }, 'create-category');
    check(created, { 'create category 201': (r) => r.status === 201 });
    if (created.status !== 201) {
      throw new Error(`create category ${name} failed: ${created.status} ${created.body}`);
    }
    categoryIds.push(created.json().id);
  }

  const prodsRes = request('GET', '/products', admin, null, 'list-products');
  check(prodsRes, { 'list products 200': (r) => r.status === 200 });
  const existingProds = jsonOrNull(prodsRes) || [];

  const products = [];
  for (let i = 0; i < PRODUCT_SPECS.length; i++) {
    const spec = PRODUCT_SPECS[i];
    const existing = findByName(existingProds, spec.name);
    if (existing) {
      products.push({ id: existing.id, name: spec.name });
      continue;
    }
    const created = request(
      'POST',
      '/products',
      admin,
      {
        productName: spec.name,
        categoryId: categoryIds[spec.category],
        description: 'k6 stress catalog',
        productType: 'SIMPLE',
      },
      'create-product',
    );
    check(created, { 'create product 201': (r) => r.status === 201 });
    if (created.status !== 201) {
      throw new Error(`create product ${spec.name} failed: ${created.status} ${created.body}`);
    }
    products.push({ id: created.json().id, name: spec.name });
  }

  return { categoryIds, products };
}

export function purchaseBody(productId, iteration) {
  return {
    type: 'PURCHASE',
    productId,
    quantity: 1.0,
    componentId: 1,
    notes: `k6-seed-${iteration}`,
  };
}

export function adjustmentBody(productId, iteration) {
  return {
    type: 'ADJUSTMENT',
    productId,
    quantity: 1.0,
    notes: `k6-seed-${iteration}`,
  };
}

export function sellBody(productId) {
  return {
    type: 'SELL',
    productId,
    quantity: 0.001,
    componentId: 1,
    behavior: 'SIMPLE',
    notes: 'k6-stress-sell',
  };
}
