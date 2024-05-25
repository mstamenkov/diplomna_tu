import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DataService } from '../data.service';

@Component({
  selector: 'app-dialog-executions',
  templateUrl: './dialog-executions.component.html',
  styleUrls: ['./dialog-executions.component.css']
})
export class DialogExecutionsComponent implements OnInit {
  constructor(public dialogRef: MatDialogRef<DialogExecutionsComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any, private dataService: DataService) { }
  
  executionId = this.data.id;

  ngOnInit(): void {
    this.dataService.getCommandDetails
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onRefresh(): void {
    this.dataService.getExecutionById(this.executionId).subscribe((result) => {
      this.data = result;
    });
    console.log(this.data);
  }
}
