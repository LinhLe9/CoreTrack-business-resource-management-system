import { useEffect, useState } from 'react'
import axios from 'axios'

export default function useProductGroups() {
  const [groups, setGroups] = useState<string[]>([])

  useEffect(() => {
    axios.get('/products/product-groups')
      .then(res => setGroups(res.data))
      .catch(err => console.error('Failed to load product groups', err))
  }, [])

  return groups
}