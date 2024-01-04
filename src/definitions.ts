export interface ScanCellPluginPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
