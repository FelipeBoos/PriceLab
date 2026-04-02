import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SimularEstrategiaPreco2 } from './simular-estrategia-preco2';

describe('SimularEstrategiaPreco2', () => {
  let component: SimularEstrategiaPreco2;
  let fixture: ComponentFixture<SimularEstrategiaPreco2>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SimularEstrategiaPreco2],
    }).compileComponents();

    fixture = TestBed.createComponent(SimularEstrategiaPreco2);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
