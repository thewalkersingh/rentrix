import { useForm, Controller, type SubmitHandler } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import type { Resolver } from "react-hook-form"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Textarea } from "@/components/ui/textarea"
import { Switch } from "@/components/ui/switch"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Loader2 } from "lucide-react"
import { flatFormSchema, type FlatFormValues } from "../schemas"
import { emptyStringToUndefined } from "../utils"
import { AddressFields } from "./AddressFields"
import type { PropertyType } from "@/types"

interface Props {
  defaultValues?: Partial<FlatFormValues>
  submitLabel: string
  onSubmit: (values: FlatFormValues) => void
  isSubmitting?: boolean
}

const PROPERTY_TYPES: { value: PropertyType; label: string }[] = [
  { value: "APARTMENT", label: "Apartment" },
  { value: "INDEPENDENT_HOUSE", label: "Independent House" },
  { value: "PG", label: "PG" },
  { value: "ROOM", label: "Room" },
  { value: "STUDIO", label: "Studio" },
  { value: "VILLA", label: "Villa" },
]

export function FlatForm({ defaultValues, submitLabel, onSubmit, isSubmitting }: Props) {
  const {
    register,
    handleSubmit,
    control,
    formState: { errors },
  } = useForm<FlatFormValues>({
    resolver: zodResolver(flatFormSchema) as Resolver<FlatFormValues>,
    defaultValues: {
      available: true,
      propertyType: "APARTMENT",
      ...defaultValues,
    },
  })

  const submit: SubmitHandler<FlatFormValues> = (values) => onSubmit(values)

  return (
    <form onSubmit={handleSubmit(submit)} className="space-y-6">
      {/* Basic details */}
      <Card>
        <CardHeader>
          <CardTitle className="text-lg">Basic details</CardTitle>
        </CardHeader>
        <CardContent className="grid gap-4 sm:grid-cols-2">
          <div className="space-y-2">
            <Label htmlFor="rent">Rent (₹) *</Label>
            <Input
              id="rent"
              type="number"
              min={0}
              step={500}
              {...register("rent", { setValueAs: emptyStringToUndefined })}
            />
            {errors.rent && <p className="text-sm text-destructive">{errors.rent.message}</p>}
          </div>

          <div className="space-y-2">
            <Label htmlFor="numberOfRooms">Rooms *</Label>
            <Input
              id="numberOfRooms"
              type="number"
              min={1}
              {...register("numberOfRooms", { setValueAs: emptyStringToUndefined })}
            />
            {errors.numberOfRooms && (
              <p className="text-sm text-destructive">{errors.numberOfRooms.message}</p>
            )}
          </div>

          <div className="space-y-2">
            <Label htmlFor="propertyType">Property type *</Label>
            <Controller
              control={control}
              name="propertyType"
              render={({ field }) => (
                <Select value={field.value} onValueChange={field.onChange}>
                  <SelectTrigger>
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    {PROPERTY_TYPES.map((p) => (
                      <SelectItem key={p.value} value={p.value}>
                        {p.label}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              )}
            />
            {errors.propertyType && (
              <p className="text-sm text-destructive">{errors.propertyType.message}</p>
            )}
          </div>

          <div className="space-y-2">
            <Label htmlFor="area">Area (m²)</Label>
            <Input
              id="area"
              type="number"
              min={0}
              step={10}
              {...register("area", { setValueAs: emptyStringToUndefined })}
            />
            {errors.area && <p className="text-sm text-destructive">{errors.area.message}</p>}
          </div>

          <div className="space-y-2">
            <Label htmlFor="floorNumber">Floor number</Label>
            <Input
              id="floorNumber"
              type="number"
              min={0}
              {...register("floorNumber", { setValueAs: emptyStringToUndefined })}
            />
            {errors.floorNumber && (
              <p className="text-sm text-destructive">{errors.floorNumber.message}</p>
            )}
          </div>

          <div className="space-y-2">
            <Label htmlFor="totalFloors">Total floors</Label>
            <Input
              id="totalFloors"
              type="number"
              min={1}
              {...register("totalFloors", { setValueAs: emptyStringToUndefined })}
            />
            {errors.totalFloors && (
              <p className="text-sm text-destructive">{errors.totalFloors.message}</p>
            )}
          </div>

          <div className="space-y-2">
            <Label htmlFor="bathrooms">Bathrooms</Label>
            <Input
              id="bathrooms"
              type="number"
              min={1}
              {...register("bathrooms", { setValueAs: emptyStringToUndefined })}
            />
            {errors.bathrooms && (
              <p className="text-sm text-destructive">{errors.bathrooms.message}</p>
            )}
          </div>

          <div className="space-y-2">
            <Label htmlFor="availableFrom">Available from</Label>
            <Input id="availableFrom" type="date" {...register("availableFrom")} />
            {errors.availableFrom && (
              <p className="text-sm text-destructive">{errors.availableFrom.message}</p>
            )}
          </div>

          <div className="space-y-4 sm:col-span-2">
            <div className="flex items-center justify-between rounded-lg border p-3">
              <div className="space-y-0.5">
                <Label htmlFor="furnished" className="cursor-pointer">
                  Furnished
                </Label>
                <p className="text-xs text-muted-foreground">Flat comes with furniture</p>
              </div>
              <Controller
                control={control}
                name="furnished"
                render={({ field }) => (
                  <Switch id="furnished" checked={!!field.value} onCheckedChange={field.onChange} />
                )}
              />
            </div>

            <div className="flex items-center justify-between rounded-lg border p-3">
              <div className="space-y-0.5">
                <Label htmlFor="parking" className="cursor-pointer">
                  Parking
                </Label>
                <p className="text-xs text-muted-foreground">Parking space included</p>
              </div>
              <Controller
                control={control}
                name="parking"
                render={({ field }) => (
                  <Switch id="parking" checked={!!field.value} onCheckedChange={field.onChange} />
                )}
              />
            </div>

            <div className="flex items-center justify-between rounded-lg border p-3">
              <div className="space-y-0.5">
                <Label htmlFor="available" className="cursor-pointer">
                  Available for rent
                </Label>
                <p className="text-xs text-muted-foreground">Uncheck if currently occupied</p>
              </div>
              <Controller
                control={control}
                name="available"
                render={({ field }) => (
                  <Switch id="available" checked={field.value} onCheckedChange={field.onChange} />
                )}
              />
            </div>
          </div>

          <div className="space-y-2 sm:col-span-2">
            <Label htmlFor="description">Description</Label>
            <Textarea
              id="description"
              rows={5}
              placeholder="Describe the flat, neighbourhood, special features…"
              {...register("description")}
            />
            {errors.description && (
              <p className="text-sm text-destructive">{errors.description.message}</p>
            )}
          </div>
        </CardContent>
      </Card>

      {/* Address */}
      <Card>
        <CardHeader>
          <CardTitle className="text-lg">Address</CardTitle>
        </CardHeader>
        <CardContent>
          <AddressFields register={register} errors={errors} />
        </CardContent>
      </Card>

      {/* Submit */}
      <div className="flex justify-end gap-2">
        <Button type="button" variant="outline" onClick={() => history.back()}>
          Cancel
        </Button>
        <Button type="submit" disabled={isSubmitting}>
          {isSubmitting && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
          {submitLabel}
        </Button>
      </div>
    </form>
  )
}
//npx shadcn@latest add switch