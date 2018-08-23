import { Component, OnInit } from '@angular/core';
import { SearchQuery } from '../../model/search-query';
import { Room } from '../../model/room';
import { Router } from '../../../../node_modules/@angular/router';

@Component({
  selector: 'app-main-search',
  templateUrl: './main-search.component.html',
  styleUrls: ['./main-search.component.css']
})
export class MainSearchComponent implements OnInit {
  searchQuery: SearchQuery;
  now: Date;
  years: Number[];
  adults: Number[];

  ngOnInit() {
    this.now = new Date();
    this.searchQuery = {
      location: "",
      filters: [],
      dates: {
        checkInDate: new Date(),
        checkOutDate: new Date(this.now.getTime() + 24*60*60*1000)
      },
      rooms: []
    }
    this.years = [];
    this.adults = [];
    for(let i=0; i<18; i++){
      this.years.push(i);
    }
    for(let i=0; i<21; i++){
      this.adults.push(i);
    }
  }

  constructor(
    private router: Router
  ) { }

  newRoom(){
    this.searchQuery.rooms.push(
      {
         adultsInRoom: 1,
         childrenInRoom: []
      }
    )
  }

  deleteRoom(room: Room){
    this.searchQuery.rooms.splice(this.searchQuery.rooms.indexOf(room), 1);
  }

  addChild(room: Room){
    room.childrenInRoom.push(0);
  }

  getName(i:number, j: number): string{
    return "name-"+i+"-"+j;
  }

  doSearch(){
    let href="search?destination="+encodeURI(this.searchQuery.location)+"&checkIn=";
    const ciDate = this.searchQuery.dates.checkInDate;
    href += ciDate.getFullYear() + "-" + (ciDate.getMonth()+1) + "-" + ciDate.getDate();
    href += "&checkOut=";
    const coDate = this.searchQuery.dates.checkOutDate;
    href += coDate.getFullYear() + "-" + (coDate.getMonth()+1) + "-" + coDate.getDate();
    href += "&rooms="+this.searchQuery.rooms.length;
    href += "&adults=";

    let children = "&children=";
    for(let i=0; i<this.searchQuery.rooms.length; i++){
      href += this.searchQuery.rooms[i].adultsInRoom;

      for(let j=0; j<this.searchQuery.rooms[i].childrenInRoom.length; j++){
        children += this.searchQuery.rooms[i].childrenInRoom[j];

        if(j < this.searchQuery.rooms[i].childrenInRoom.length -1){
          children += ",";
        }
      }

      if(i < this.searchQuery.rooms.length -1){
        href += ",";
        children += ";";
      }
    }

    href += children;
    this.router.navigateByUrl(href);
  }
}
