import { NgModule } from '@angular/core';
import { SharedModule } from 'src/app/shared/shared.module';
import { HomeRoutingModule } from './home-routing.module';
import { StatsModule } from '../stats/stats.module';
import { HomeComponent } from './home/home.component';
import { NavBarModule } from '../navbar/navbar.module';

@NgModule({
  declarations: [ HomeComponent ],
  imports: [ SharedModule, HomeRoutingModule, NavBarModule, StatsModule ]
})
export class HomeModule {}
