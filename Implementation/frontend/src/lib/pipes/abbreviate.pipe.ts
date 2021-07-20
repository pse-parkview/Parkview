import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'abbreviate'
})
export class AbbreviatePipe implements PipeTransform {

  transform(value: string, length: number): string {
    return value.length > length ? value.substr(0, length) + '…' : value;
  }

}
