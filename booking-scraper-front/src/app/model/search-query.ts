import { Dates } from "./dates";
import { Room } from "./room";

export class SearchQuery {
    constructor(
        public location: string,
        public dates: Dates,
        public rooms: Room[],
        public filters: string[],
        public markers: any[]
    ){}
}
