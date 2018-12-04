/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
import { Injectable } from '@angular/core';
import { throwError, Observable } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {CartModel} from '../model/cart.model';


@Injectable()
export class CartService  {

  private headers = new HttpHeaders()
    .append('Content-Type', 'application/json')
    .append('Access-Control-Allow-Headers', 'Content-Type')
    .append('Access-Control-Allow-Methods', 'GET')
    .append('Access-Control-Allow-Origin', '*');

  constructor(protected http: HttpClient) {
  }

  public getCarts(): Observable<Object[]> {
    const url = '/api/cartAbandonment/carts';

    return this.http.get<CartModel[]>(url, {headers: this.headers}).pipe(
      map((result) => {
        // @ts-ignore
        const res: CartModel[] = result.carts;
        return res;
      }),
      catchError((error) => {
        return throwError(error);
      })
    );
  }
}
