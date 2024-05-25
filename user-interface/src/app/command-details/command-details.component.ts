import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DataService } from '../data.service';
import { MatDialog } from '@angular/material/dialog';
import { DialogComponent } from '../dialog/dialog.component';
import { MatSnackBar } from '@angular/material/snack-bar'
import { FormControl, FormGroup, Validators } from '@angular/forms'
import { DialogExecutionsComponent } from '../dialog-executions/dialog-executions.component';

@Component({
  selector: 'app-command-details',
  templateUrl: './command-details.component.html',
  styleUrls: ['./command-details.component.css']
})

export class CommandDetailsComponent implements OnInit {
  constructor(private route: ActivatedRoute, private commandService: DataService, public dialog: MatDialog,
    private snackBar: MatSnackBar, private router: Router) {
    commandService.event_callback.subscribe(error => {
      this.openSnackBar(error);
    })
  }

  id: string = String(this.route.snapshot.paramMap.get('id'));
  commandDetails: any = {};
  command: any = {};
  request: any = {};
  resultDialogData: any = {};
  inputKeysGroup = new FormGroup({}, [Validators.required]);
  displayedColumns: string[] = ['name', 'description', 'tags'];

  ngOnInit(): void {
    this.commandService.getCommandDetails(this.id).subscribe((res => {
      this.commandDetails = res;
      for (let key in this.commandDetails.inputKeys) {
        this.inputKeysGroup.addControl(key, new FormControl());
      }
    }));
  }

  typeOf(variable: any) {
    return typeof variable;
  }

  openDialog(data: any): void {
    console.log(this.commandDetails);
    let dialogRef = this.dialog.open(DialogComponent, {
      width: '500px',
      height: 'auto',
      data: {
        command: data,
        inputKeysForm: this.inputKeysGroup,
        paramType: this.commandDetails.inputKeys
      },
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log(result);
      if (result) {
        try {
          for (let key in this.commandDetails.inputKeys) {
            if (this.commandDetails.inputKeys[key] === 'Integer') {
              result.inputKeysForm.value[key] = Number(result.inputKeysForm.value[key]);
            }
            if (this.commandDetails.inputKeys[key] === 'Object') {
              result.inputKeysForm.value[key] = JSON.parse(result.inputKeysForm.value[key]);
            }
          }
          this.request.inputKeys = (result.inputKeysForm.value);
          this.request.commandName = this.commandDetails.name;
          if (result.command.tagsAsString)
            this.request.tags = result.command.tagsAsString.split(',');
          console.log(this.request);
          this.commandService.postRequest(this.request).subscribe((data) => {
            this.processResponse(data);
          });
          this.request = {};
          this.command.tagsAsString = null;
        } catch (e) {
          console.log(e);
          this.openSnackBar(e);
          this.inputKeysGroup.reset();
        }
      }
      this.command = {};
    });
  }

  deleteCommand(): void {
    console.log(this.commandDetails);
    this.commandService.deleteRequest(this.commandDetails.id).subscribe((data) => {
      this.router.navigate(['commands']);
      this.processResponse(data);
    });
  }

  openResultDialog(data: any): void {
    let dialogRef = this.dialog.open(DialogExecutionsComponent, {
      width: '500px',
      height: 'auto',
      data: data
    });

    dialogRef.afterClosed().subscribe(() => {
      this.resultDialogData.error = '';
    })
  }

  processResponse(response: any) {
    console.log(response);
    this.resultDialogData.status = response.status;
    this.resultDialogData.inputKeys = response.inputKeys;
    this.resultDialogData.id = response.id;
    if (response.status === 'FINISHED') {
      this.resultDialogData.outputKeys = response.outputKeys;
    } else {
      this.resultDialogData.error = response.error;
    }
    this.openResultDialog(this.resultDialogData);
    this.inputKeysGroup.reset();
  }

  openSnackBar(error: any): any {
    let errorMessage = '';
    errorMessage = `Error: ${error.error.error}` + ` - ${error.error.reason}`;
    this.snackBar.open(errorMessage, "Close", { duration: 10000 });
    console.error(error.message);
    return errorMessage;
  }
}