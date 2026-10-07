import { z } from "zod"

const PROPERTY_TYPES = ["APARTMENT", "INDEPENDENT_HOUSE", "PG", "ROOM", "STUDIO", "VILLA"] as const

export const flatFormSchema = z.object({
  // Basic
  rent: z
    .number({ error: "Rent is required" })
    .positive("Rent must be positive")
    .max(10_000_000, "Rent looks unreasonable"),
  numberOfRooms: z
    .number({ error: "Number of rooms is required" })
    .int("Must be a whole number")
    .min(1, "At least 1 room")
    .max(20, "Too many rooms"),
  area: z.number().positive("Area must be positive").optional(),
  floorNumber: z.number().int().min(0, "Floor must be ≥ 0").optional(),
  totalFloors: z.number().int().min(1, "Total floors must be ≥ 1").optional(),
  bathrooms: z.number().int().min(1, "At least 1 bathroom").max(20).optional(),
  furnished: z.boolean().optional(),
  parking: z.boolean().optional(),
  availableFrom: z.string().optional(),
  propertyType: z.enum(PROPERTY_TYPES, { error: "Select a property type" }),
  description: z.string().max(4000, "Description too long").optional(),
  available: z.boolean().default(true),

  // Address
  addressLine: z.string().max(255).optional(),
  street: z.string().max(255).optional(),
  city: z.string().min(1, "City is required").max(100),
  state: z.string().max(100).optional(),
  zipCode: z.string().max(20).optional(),
})

export type FlatFormValues = z.infer<typeof flatFormSchema>