import {PlotConfiguration, SupportedChartType} from "../../logic/plothandler/interfaces/plot-configuration";
import {ParamMap} from "@angular/router";
import {ChartDataSets} from "chart.js";

export class PlotUtils {

  public static readonly colors: string[] = [
    '#BF616A',
    '#D08770',
    '#EBCB8B',
    '#A3BE8C',
    '#B48EAD'
  ];

  private constructor() {
    // utility class constructor
  }

  public static parsePlotConfig(paramMap: ParamMap): PlotConfiguration | undefined {
    const mandatoryParams: string[] = [
      'benchmark',
      'commits',
      'devices',
      'plotType',
      'labelForTitle',
      'labelForXAxis',
      'labelForYAxis',
      'chartType'
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
        options: extraOptions,
        chartType: paramMap.get('chartType') as SupportedChartType,
      };
    }
    return undefined;
  }

  public static downloadCanvas(event: MouseEvent, fileName: string = 'plot.png') {
    const anchor = event.target as HTMLAnchorElement;
    const canvas = document.getElementById('canvas') as HTMLCanvasElement;
    const ctx = canvas.getContext('2d');

    ctx!.globalCompositeOperation = 'destination-over';
    ctx!.fillStyle = 'white';
    ctx!.fillRect(0, 0, canvas.width, canvas.height);

    anchor.href = canvas.toDataURL();
    anchor.download = fileName;
  }

  public static colorizeDataSet(dataSets: ChartDataSets[]): ChartDataSets[] {
    dataSets.forEach((d: ChartDataSets, index: number) => {
      const color = PlotUtils.getColor(index);
      d.pointBackgroundColor = color;
      d.pointBorderColor = color;
      d.borderColor = color;
      d.backgroundColor = color;
    });
    return dataSets;
  }

  private static getColor(i: number) {
    if (i < PlotUtils.colors.length) {
      return PlotUtils.colors[i];
    } else {
      return `rgba(${Math.floor(Math.random() * 255)}, ${Math.floor(Math.random() * 255)}, ${Math.floor(Math.random() * 255)}, 255)`;
    }
  }
}
