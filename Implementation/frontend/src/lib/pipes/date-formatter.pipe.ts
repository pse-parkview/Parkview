import { Pipe, PipeTransform } from '@angular/core';

export type DateFormatTypes = 'medium';

@Pipe({
  name: 'dateFormatter'
})
export class DateFormatterPipe implements PipeTransform {

  transform(unparsedValue: string, format: DateFormatTypes): string {
    const value = new Date(unparsedValue);
    switch (format) {
      case 'medium':
        return value.toLocaleString();
    }
    return value.toString();
  }
}
