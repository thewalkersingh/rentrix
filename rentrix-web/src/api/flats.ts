import type {
  CreateFlatRequest,
  Flat,
  FlatFilters,
  FlatImage,
  FlatSummary,
  UpdateFlatRequest,
} from "@/types"
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

  // ── Images ───────────────────────────────────────────────────────────

  listImages: (flatId: number): Promise<FlatImage[]> =>
    api.get(`/flats/${flatId}/images`).then((r) => r.data),

  uploadImage: (flatId: number, file: File): Promise<FlatImage> => {
    const formData = new FormData()
    formData.append("file", file)
    return api
      .post(`/flats/${flatId}/images`, formData, {
        headers: { "Content-Type": "multipart/form-data" },
      })
      .then((r) => r.data)
  },

  deleteImage: (flatId: number, imageId: number): Promise<void> =>
    api.delete(`/flats/${flatId}/images/${imageId}`).then(() => undefined),

  setPrimaryImage: (flatId: number, imageId: number): Promise<FlatImage> =>
    api.patch(`/flats/${flatId}/images/${imageId}/primary`).then((r) => r.data),
}