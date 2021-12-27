import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
  name: 'abbreviateSha'
})
export class AbbreviateShaPipe implements PipeTransform {

  transform(value: string, length: number = 7): string {
    return value.length > length ? value.substr(0, length) : value;
  }

}
