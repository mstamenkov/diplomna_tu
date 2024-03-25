import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { DataService } from '../data.service';
import { DialogExecutionsComponent } from '../dialog-executions/dialog-executions.component';

@Component({
  selector: 'app-exucutions',
  templateUrl: './exucutions.component.html',
  styleUrls: ['./exucutions.component.css']
})
export class ExucutionsComponent implements OnInit {
  data: any;
  displayedColumns: string[] = ['id', 'type', 'status', 'tags'];
  constructor(private executionService: DataService, private dialog: MatDialog) { }

  ngOnInit(): void {
    this.executionService.getExecutions().subscribe((result) => {
      this.data = result;
    })
  }

  openDialog(execution: any): void {
    this.dialog.open(DialogExecutionsComponent, {
      width: '500px',
      height: 'auto',
      data: execution
    });
  }

  getExecutionById(id: any) {
    this.executionService.getExecutionById(id).subscribe((result) => {
      this.openDialog(result);
    })
  }
}
