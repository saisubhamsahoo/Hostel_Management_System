export interface Room {
  id?: number;
  roomNumber: string;
  roomType: 'SINGLE' | 'DOUBLE' | 'TRIPLE' | 'FOUR_SHARING';
  totalBeds: number;
  occupiedBeds: number;
  floor: string;
  available: boolean;
  availableBeds?: number;
}
