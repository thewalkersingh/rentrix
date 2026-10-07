import type { Flat, CreateFlatRequest, UpdateFlatRequest } from "@/types"
import type { FlatFormValues } from "./schemas"

/** Converts form values → backend create request */
export function toCreateRequest(values: FlatFormValues): CreateFlatRequest {
  return {
    rent: values.rent,
    numberOfRooms: values.numberOfRooms,
    area: values.area,
    floorNumber: values.floorNumber,
    totalFloors: values.totalFloors,
    furnished: values.furnished,
    bathrooms: values.bathrooms,
    parking: values.parking,
    availableFrom: values.availableFrom || undefined,
    propertyType: values.propertyType,
    description: values.description,
    available: values.available,
    address: {
      addressLine: values.addressLine,
      street: values.street,
      city: values.city,
      state: values.state,
      zipCode: values.zipCode,
    },
  }
}

/** Converts form values → backend update request (same shape) */
export function toUpdateRequest(values: FlatFormValues): UpdateFlatRequest {
  return toCreateRequest(values)
}

/** Converts a Flat entity → form values (for edit) */
export function toFormValues(flat: Flat): Partial<FlatFormValues> {
  return {
    rent: flat.rent,
    numberOfRooms: flat.numberOfRooms,
    area: flat.area,
    floorNumber: flat.floorNumber,
    totalFloors: flat.totalFloors,
    furnished: flat.furnished,
    bathrooms: flat.bathrooms,
    parking: flat.parking,
    availableFrom: flat.availableFrom ?? undefined,
    propertyType: flat.propertyType,
    description: flat.description,
    available: flat.available,
    addressLine: flat.address.addressLine,
    street: flat.address.street,
    city: flat.address.city ?? "",
    state: flat.address.state,
    zipCode: flat.address.zipCode,
  }
}

/**
 * RHF helper: converts empty string from number input → undefined.
 * Use as `setValueAs` on number inputs.
 */
export const emptyStringToUndefined = (v: unknown) => {
  if (v === "" || v === null || v === undefined) return undefined
  const n = Number(v)
  return Number.isNaN(n) ? undefined : n
}