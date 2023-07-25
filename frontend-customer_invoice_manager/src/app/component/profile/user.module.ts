import { NgModule } from '@angular/core';
import { SharedModule } from 'src/app/shared/shared.module';
import { UserRoutingModule } from './user-routing.module';
import { UserComponent } from './user/user.component';
import { NavBarModule } from '../navbar/navbar.module';

@NgModule({
  declarations: [ UserComponent ],
  imports: [ SharedModule, UserRoutingModule, NavBarModule ]
})
export class UserModule {}
