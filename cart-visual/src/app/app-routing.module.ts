import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { DashboardComponent } from './dashboard/dashboard.component';


const routes: Routes = [
  { path: 'dashboard', component: DashboardComponent },
  { path: '', redirectTo: '/dashboard', pathMatch: 'full'},
];


@NgModule({
  //  initialize the router and start it listening for browser location changes
  imports: [
    RouterModule.forRoot(routes, {
      paramsInheritanceStrategy: 'emptyOnly' // default
    }),
  ],
  exports: [ RouterModule ]
})
export class AppRoutingModule {}
