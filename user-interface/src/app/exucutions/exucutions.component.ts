import { Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { DataService } from '../data.service';
import { DialogExecutionsComponent } from '../dialog-executions/dialog-executions.component';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';

@Component({
  selector: 'app-exucutions',
  templateUrl: './exucutions.component.html',
  styleUrls: ['./exucutions.component.css']
})
export class ExucutionsComponent implements OnInit {
  data: any;
  displayedColumns: string[] = ['id', 'commandName', 'status', 'tags'];
  @ViewChild(MatSort, { static: true }) sort: MatSort = new MatSort();
  constructor(private executionService: DataService, private dialog: MatDialog) { }

  ngOnInit(): void {
    this.executionService.getExecutions().subscribe((result) => {
      this.data = new MatTableDataSource(result as Object[]);
      this.data.sort = this.sort;
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
