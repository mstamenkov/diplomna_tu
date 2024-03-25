import { Component, Inject, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DataService } from '../data.service';

@Component({
  selector: 'app-dialog-executions',
  templateUrl: './dialog-executions.component.html',
  styleUrls: ['./dialog-executions.component.css']
})
export class DialogExecutionsComponent implements OnInit {
  constructor(public dialogRef: MatDialogRef<DialogExecutionsComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any, private dataService: DataService, private router: Router) { }

  ngOnInit(): void {
    this.dataService.getCommandDetails
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
