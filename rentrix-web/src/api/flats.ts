import type { CreateFlatRequest, Flat, FlatFilters, FlatSummary, UpdateFlatRequest } from "@/types"
import type { Page } from "@/types"
import { api } from "./client"

export const flatsApi = {
  list: (filters: FlatFilters = {}): Promise<Page<FlatSummary>> =>
    api.get("/flats", { params: filters }).then((r) => r.data),

  listMine: (page = 0, size = 20): Promise<Page<FlatSummary>> =>
    api.get("/flats/me", { params: { page, size } }).then((r) => r.data),

  get: (id: number): Promise<Flat> => api.get(`/flats/${id}`).then((r) => r.data),

  create: (payload: CreateFlatRequest): Promise<Flat> =>
    api.post("/flats", payload).then((r) => r.data),

  update: (id: number, payload: UpdateFlatRequest): Promise<Flat> =>
    api.patch(`/flats/${id}`, payload).then((r) => r.data),

  remove: (id: number): Promise<void> => api.delete(`/flats/${id}`).then(() => undefined),
}