
declare module "@capacitor/core" {
  interface PluginRegistry {
      ScanCellPlugin: ScanCellPluginPlugin
  }
}



export interface ScanCellPluginPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
  testPluginMethod(options: { msg: string }): Promise<{value: string}>;
}
