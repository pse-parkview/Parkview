import {Injectable} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class RestService {


  private readonly URL: string;
  json = [
    {"x": 4054.0, "y": 4.603355E7, "label": "coo"},
    {"x": 4054.0, "y": 6633021.6, "label": "hybrid"},
    {"x": 4054.0, "y": 12966.5, "label": "csr"},
    {"x": 4054.0, "y": 17339.3, "label": "sellp"},
    {"x": 4054.0, "y": 23980.8, "label": "ell"},
  ];

  requestData() {
    return this.json;
  }

  constructor(url: string) {
    this.URL = url;
  }
}
