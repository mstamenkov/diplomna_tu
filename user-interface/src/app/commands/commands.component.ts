import { Component, OnInit } from '@angular/core';
import { DataService } from '../data.service';

@Component({
  selector: 'app-commands',
  templateUrl: './commands.component.html',
  styleUrls: ['./commands.component.css']
})
export class CommandsComponent implements OnInit {

  constructor(private dataService : DataService) { }
  displayedColumns: string[] = ['name', 'description', 'tags'];
  data : any;
  ngOnInit(): void {
    this.dataService.getCommands().subscribe((res) => {
      this.data = res;
    });
  }
}
