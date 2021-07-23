import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'dateFormatter'
})
export class DateFormatterPipe implements PipeTransform {

  transform(value: Date, format: 'shortDateTime'): string {
    return value.toDateString();
  }

}
