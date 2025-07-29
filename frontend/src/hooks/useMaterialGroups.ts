import { useEffect, useState } from 'react'
import api from '@/lib/axios'
import { MaterialGroup } from '@/types/material'

export default function useMaterialGroups() {
  const [groups, setGroups] = useState<MaterialGroup[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    setLoading(true)
    api.get('/materials/material-groups')
      .then(res => {
        setGroups(res.data)
        setLoading(false)
      })
      .catch(err => {
        console.error('Failed to load material groups', err)
        setLoading(false)
      })
  }, [])

  return { materialGroups: groups, loading }
}