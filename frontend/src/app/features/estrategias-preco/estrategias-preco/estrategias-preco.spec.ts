import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EstrategiasPreco } from './estrategias-preco';

describe('EstrategiasPreco', () => {
  let component: EstrategiasPreco;
  let fixture: ComponentFixture<EstrategiasPreco>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EstrategiasPreco],
    }).compileComponents();

    fixture = TestBed.createComponent(EstrategiasPreco);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
