import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
  name: 'defaultvalue'
})
export class DefaultvaluePipe implements PipeTransform {

  transform(value: string, defaultvalue: string): string {
    return value.trim() === '' ? defaultvalue : value;
  }

}
