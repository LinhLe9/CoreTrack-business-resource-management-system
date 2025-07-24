import { useEffect, useState } from 'react'
import api from '@/lib/axios'
import { Group } from '@/types/product'

export default function useProductGroups() {
  const [groups, setGroups] = useState<Group[]>([])

  useEffect(() => {
    api.get('/products/product-groups')
      .then(res => setGroups(res.data))
      .catch(err => console.error('Failed to load product groups', err))
  }, [])

  return groups
}