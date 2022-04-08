import {Pipe, PipeTransform} from '@angular/core';

export type DateFormatTypes = 'medium' | 'short';

@Pipe({
  name: 'dateFormatter'
})
export class DateFormatterPipe implements PipeTransform {

  transform(unparsedValue: string | Date, format: DateFormatTypes): string {
    const value = new Date(unparsedValue);
    switch (format) {
      case 'short':
        return value.toLocaleDateString();
      case 'medium':
        return value.toLocaleString();
    }
    return value.toString();
  }
}
