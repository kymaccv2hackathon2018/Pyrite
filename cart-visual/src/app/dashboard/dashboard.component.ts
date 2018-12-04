import { Component, OnInit } from '@angular/core';
import {CartService} from '../services/cart.service';
import {interval, Observable} from 'rxjs';
import { tap } from 'rxjs/operators';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

  protected carts: Observable<Object[]>;

  private refreshInterval = 20000;

  constructor(private cartService: CartService) { }

  ngOnInit() {
    interval(this.refreshInterval).pipe(
      tap(() => this.fetchCarts())
    ).subscribe();

    this.fetchCarts();
  }

  private fetchCarts(): void {
    this.carts = this.cartService.getCarts();
  }
}
