import { Injectable } from '@angular/core';
import {MatSnackBar, MatSnackBarConfig} from "@angular/material/snack-bar";

@Injectable({
  providedIn: 'root'
})
export class SnackBarService {

  constructor(private readonly snackBar: MatSnackBar) {
  }

  notify(message: string) {
    const notificationSnackBarConfig: MatSnackBarConfig = {
      duration: 2500
    }
    this.snackBar.open(message, 'OK', notificationSnackBarConfig);
  }
}
