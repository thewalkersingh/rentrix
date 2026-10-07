import type { FieldErrors, UseFormRegister } from "react-hook-form"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import type { FlatFormValues } from "../schemas"

interface Props {
  register: UseFormRegister<FlatFormValues>
  errors: FieldErrors<FlatFormValues>
}

export function AddressFields({ register, errors }: Props) {
  return (
    <div className="grid gap-4 sm:grid-cols-2">
      <div className="space-y-2 sm:col-span-2">
        <Label htmlFor="addressLine">Address line</Label>
        <Input id="addressLine" placeholder="A-101 Green Heights" {...register("addressLine")} />
        {errors.addressLine && (
          <p className="text-sm text-destructive">{errors.addressLine.message}</p>
        )}
      </div>

      <div className="space-y-2 sm:col-span-2">
        <Label htmlFor="street">Street</Label>
        <Input id="street" placeholder="MG Road" {...register("street")} />
        {errors.street && <p className="text-sm text-destructive">{errors.street.message}</p>}
      </div>

      <div className="space-y-2">
        <Label htmlFor="city">City *</Label>
        <Input id="city" placeholder="Pune" {...register("city")} />
        {errors.city && <p className="text-sm text-destructive">{errors.city.message}</p>}
      </div>

      <div className="space-y-2">
        <Label htmlFor="state">State</Label>
        <Input id="state" placeholder="Maharashtra" {...register("state")} />
        {errors.state && <p className="text-sm text-destructive">{errors.state.message}</p>}
      </div>

      <div className="space-y-2">
        <Label htmlFor="zipCode">Zip code</Label>
        <Input id="zipCode" placeholder="411001" {...register("zipCode")} />
        {errors.zipCode && <p className="text-sm text-destructive">{errors.zipCode.message}</p>}
      </div>
    </div>
  )
}