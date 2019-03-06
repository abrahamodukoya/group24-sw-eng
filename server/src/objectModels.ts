export interface IUsers {
    _id: string;
    years: IYear[];
  }
  
  interface IYear {
    year: string;
    weeks: IWeek[];
  }
  interface IWeek {
    week: string;
    days: IDay[];
  }
  interface IDay {
    day: string;
  }
  