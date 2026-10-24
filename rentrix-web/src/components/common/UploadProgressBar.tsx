interface Props {
  percent: number
  label?: string
}

export function UploadProgressBar({ percent, label }: Props) {
  return (
    <div className="space-y-1">
      {label && (
        <div className="flex items-center justify-between text-xs text-muted-foreground">
          <span>{label}</span>
          <span>{percent}%</span>
        </div>
      )}
      <div className="h-1.5 w-full overflow-hidden rounded-full bg-muted">
        <div
          className="h-full bg-primary transition-all duration-200"
          style={{ width: `${percent}%` }}
        />
      </div>
    </div>
  )
}