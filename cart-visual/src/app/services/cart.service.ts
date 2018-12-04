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
import {HttpClient} from '@angular/common/http';


@Injectable()
export class CartService  {

  constructor(protected http: HttpClient) {
  }


  public getCarts(): Observable<Object[]> {
    const url = '';
    const params: any = undefined;

    return this.http.get(url, {params}).pipe(
      map((result: Object[]) => {
        // @ts-ignore
        const res: Object[] = {carts: result.carts};
        return res;
      }),
      catchError((error) => {
        return throwError(error);
      })
    );
  }
}
