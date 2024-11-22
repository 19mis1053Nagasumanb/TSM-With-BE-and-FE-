import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RangefiltersComponent } from './rangefilters.component';

describe('RangefiltersComponent', () => {
  let component: RangefiltersComponent;
  let fixture: ComponentFixture<RangefiltersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RangefiltersComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(RangefiltersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
