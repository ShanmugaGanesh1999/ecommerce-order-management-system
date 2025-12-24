export interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  stockQuantity: number;
  categoryId: number;
  categoryName?: string;
  imageUrl?: string;
  sku: string;
  isActive: boolean;
}

export interface Category {
  id: number;
  name: string;
  description?: string;
}
