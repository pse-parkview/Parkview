import {NgModule} from '@angular/core';
import {HttpClientModule} from "@angular/common/http";
import {CommonModule} from '@angular/common';
import {MatDialogModule} from "@angular/material/dialog";
import {CookieModule} from "ngx-cookie";
import {CookieService} from "./cookiehandler/cookie.service";


@NgModule({
  declarations: [],
  exports: [],
  imports: [
    HttpClientModule,
    CommonModule,
    MatDialogModule,
    CookieModule.forChild()
  ],
  providers: [
    CookieService
  ]
})
export class LogicModule {
}
