import { PlotUtils } from './plot-utils';
import {PlotConfiguration} from "../../logic/plothandler/interfaces/plot-configuration";

describe('PlotUtils', () => {
  it('should identify valid plot configs', () => {
    const validPlotConfig: PlotConfiguration = {
      benchmark: 'testName',
      commits: ['SHA1', 'SHA2'],
      devices: ['DEVICE1', 'DEVICE2'],
      plotType: 'mockTestPlot',
      labelForTitle: 'mockButValidLabel',
      labelForXAxis: 'mockButValidXAxis',
      labelForYAxis: 'mockButValidYAxis',
      options: {},
      chartType: 'line'
    }
    expect(PlotUtils.isValidConfig(validPlotConfig)).toBe(true);
  });

  it('should identify invalid plot configs', () => {
    const invalidPlotConfig: PlotConfiguration = {
      benchmark: '',
      commits: ['SHA1'],
      devices: ['DEVICE1', 'DEVICE2'],
      plotType: '',
      labelForTitle: '',
      labelForXAxis: '',
      labelForYAxis: '',
      options: {},
      chartType: 'line'
    }
    expect(PlotUtils.isValidConfig(invalidPlotConfig)).toBe(false);
  });
});
