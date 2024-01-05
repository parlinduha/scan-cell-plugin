import { WebPlugin } from '@capacitor/core';

import type { ScanCellPluginPlugin } from './definitions';

export class ScanCellPluginWeb extends WebPlugin implements ScanCellPluginPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }

  async testPluginMethod(options: { msg: string; }): Promise<{ value: string; }> {
    alert(options.msg);
    return {value: options.msg}
  }
}
