import {PlotConfiguration} from "../../logic/plothandler/interfaces/plot-configuration";
import {ParamMap} from "@angular/router";

export class PlotUtils {
  private constructor() {
  }

  public static parsePlotConfig(paramMap: ParamMap): PlotConfiguration | undefined {
    const mandatoryParams: string[] = [
      'benchmark',
      'commits',
      'devices',
      'plotType',
      'labelForTitle',
      'labelForXAxis',
      'labelForYAxis'
    ];

    if (!mandatoryParams.some(k => !paramMap.has(k))) { // if there arent any mising mandatory keys
      const extraOptions: { [key: string]: string } = {};
      paramMap.keys.filter(k => !mandatoryParams.includes(k))
        .forEach((k => extraOptions[k] = paramMap.get(k) as string));

      return {
        benchmark: paramMap.get("benchmark") as string,
        commits: paramMap.getAll("commits"),
        devices: paramMap.getAll("devices"),
        plotType: paramMap.get("plotType") as string,
        labelForTitle: paramMap.get('labelForTitle') as string,
        labelForXAxis: paramMap.get('labelForXAxis') as string,
        labelForYAxis: paramMap.get('labelForYAxis') as string,
        options: extraOptions
      };
    }
    return undefined;
  }
}
