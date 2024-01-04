import { registerPlugin } from '@capacitor/core';

import type { ScanCellPluginPlugin } from './definitions';

const ScanCellPlugin = registerPlugin<ScanCellPluginPlugin>('ScanCellPlugin', {
  web: () => import('./web').then(m => new m.ScanCellPluginWeb()),
});

export * from './definitions';
export { ScanCellPlugin };
