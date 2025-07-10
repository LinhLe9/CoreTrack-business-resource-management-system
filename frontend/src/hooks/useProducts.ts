import { useEffect, useState } from 'react';
import { getProducts } from '../services/productService';
import { Product, ProductQueryParams} from '../types/product';
import { PageResponse } from '../types/PageResponse';

export default function useProducts(filters: ProductQueryParams) {
  const [data, setData] = useState<PageResponse<Product>>();
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<any>(null);

  useEffect(() => {
    setLoading(true);
    getProducts(filters)
      .then(res => {
        setData(res);
        setLoading(false);
      })
      .catch(err => {
        console.error(err);
        setError(err);
        setLoading(false);
      });
  }, [JSON.stringify(filters)]); // re-fetch when filters change

  return { data, loading, error };
}