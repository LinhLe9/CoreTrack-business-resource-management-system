import { useEffect, useState } from 'react'
import api from '@/lib/axios'
import { Group } from '@/types/material'

export default function useMaterialGroups() {
  const [groups, setGroups] = useState<Group[]>([])

  useEffect(() => {
    api.get('/materials/material-groups')
      .then(res => setGroups(res.data))
      .catch(err => console.error('Failed to load material groups', err))
  }, [])

  return groups
}