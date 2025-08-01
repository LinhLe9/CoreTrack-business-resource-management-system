import { useState, useCallback } from 'react';
import { 
  getProducts, 
  getAllProductsForAutocomplete, 
  getAllProductVariantsForAutocomplete,
  getProductById,
  updateProduct,
  getAllProductGroups,
  getAvailableStatusTransitions,
  changeProductStatus,
  getBomItem
} from '../services/productService';
import { 
  Product, 
  ProductDetailResponse, 
  ProductQueryParams, 
  ProductAutoComplete, 
  ProductVariantAutoComplete,
  BOMItemResponse
} from '../types/product';
import { PageResponse } from '../types/PageResponse';

export const useProduct = () => {
  // State
  const [products, setProducts] = useState<PageResponse<Product> | null>(null);
  const [currentProduct, setCurrentProduct] = useState<ProductDetailResponse | null>(null);
  const [bomItems, setBomItems] = useState<BOMItemResponse[]>([]);
  const [autocompleteResults, setAutocompleteResults] = useState<ProductAutoComplete[]>([]);
  const [variantAutocompleteResults, setVariantAutocompleteResults] = useState<ProductVariantAutoComplete[]>([]);

  // Loading states
  const [loading, setLoading] = useState(false);
  const [loadingBom, setLoadingBom] = useState(false);
  const [updating, setUpdating] = useState(false);
  const [changingStatus, setChangingStatus] = useState(false);

  // Error states
  const [error, setError] = useState<string | null>(null);

  // Clear error
  const clearError = useCallback(() => {
    setError(null);
  }, []);

  // Get products with filtering
  const fetchProducts = useCallback(async (params: ProductQueryParams = {}) => {
    setLoading(true);
    setError(null);
    try {
      const result = await getProducts(params);
      setProducts(result);
      return result;
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || err.message || 'Failed to fetch products';
      setError(errorMessage);
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  // Get product by ID
  const fetchProductById = useCallback(async (id: number) => {
    setLoading(true);
    setError(null);
    try {
      const result = await getProductById(id);
      setCurrentProduct(result);
      return result;
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || err.message || 'Failed to fetch product';
      setError(errorMessage);
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  // Get BOM items for a product variant
  const fetchBomItems = useCallback(async (productId: number, variantId: number) => {
    setLoadingBom(true);
    setError(null);
    try {
      const result = await getBomItem(productId, variantId);
      setBomItems(result);
      return result;
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || err.message || 'Failed to fetch BOM items';
      setError(errorMessage);
      throw err;
    } finally {
      setLoadingBom(false);
    }
  }, []);

  // Get autocomplete results
  const fetchAutocomplete = useCallback(async (search: string) => {
    if (!search || search.trim() === '') {
      setAutocompleteResults([]);
      return;
    }
    
    try {
      const results = await getAllProductsForAutocomplete();
      setAutocompleteResults(results);
      return results;
    } catch (err) {
      console.error("Error fetching autocomplete:", err);
    }
  }, []);

  // Get variant autocomplete results
  const fetchVariantAutocomplete = useCallback(async (search: string) => {
    if (!search || search.trim() === '') {
      setVariantAutocompleteResults([]);
      return;
    }
    
    try {
      const results = await getAllProductVariantsForAutocomplete(search);
      setVariantAutocompleteResults(results);
      return results;
    } catch (err) {
      console.error("Error fetching variant autocomplete:", err);
    }
  }, []);

  // Update product
  const updateProductById = useCallback(async (id: number, data: any) => {
    setUpdating(true);
    setError(null);
    try {
      const result = await updateProduct(id, data);
      // Refresh current product after update
      await fetchProductById(id);
      return result;
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || err.message || 'Failed to update product';
      setError(errorMessage);
      throw err;
    } finally {
      setUpdating(false);
    }
  }, [fetchProductById]);

  // Change product status
  const changeProductStatusById = useCallback(async (productId: number, newStatus: string, reason?: string) => {
    setChangingStatus(true);
    setError(null);
    try {
      const result = await changeProductStatus(productId, newStatus, reason);
      // Refresh current product after status change
      await fetchProductById(productId);
      return result;
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || err.message || 'Failed to change product status';
      setError(errorMessage);
      throw err;
    } finally {
      setChangingStatus(false);
    }
  }, [fetchProductById]);

  // Get available status transitions
  const getStatusTransitions = useCallback(async (productId: number) => {
    setLoading(true);
    setError(null);
    try {
      const result = await getAvailableStatusTransitions(productId);
      return result;
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || err.message || 'Failed to get status transitions';
      setError(errorMessage);
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  // Get all product groups
  const fetchProductGroups = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const result = await getAllProductGroups();
      return result;
    } catch (err: any) {
      const errorMessage = err.response?.data?.message || err.message || 'Failed to fetch product groups';
      setError(errorMessage);
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  return {
    // State
    products,
    currentProduct,
    bomItems,
    autocompleteResults,
    variantAutocompleteResults,
    loading,
    loadingBom,
    updating,
    changingStatus,
    error,

    // Actions
    fetchProducts,
    fetchProductById,
    fetchBomItems,
    fetchAutocomplete,
    fetchVariantAutocomplete,
    updateProductById,
    changeProductStatusById,
    getStatusTransitions,
    fetchProductGroups,
    clearError,
  };
}; 