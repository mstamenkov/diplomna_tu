import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CommandDetailsComponent } from './command-details/command-details.component';
import { CommandsComponent } from './commands/commands.component';
import { DialogExecutionsComponent } from './dialog-executions/dialog-executions.component';
import { ExucutionsComponent } from './exucutions/exucutions.component';

const routes: Routes = [{
    path: 'command-details/:id', component: CommandDetailsComponent
  },{
    path: 'commands', component: CommandsComponent
  },{
    path: 'executions', component: ExucutionsComponent
  },{
    path: 'execution-details/:id', component: ExucutionsComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
