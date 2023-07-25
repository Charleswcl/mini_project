import { NgModule } from '@angular/core';
import { ExtractArrayValue } from '../pipes/extractvalue.pipe';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@NgModule({
    declarations: [ExtractArrayValue],
    imports: [RouterModule, CommonModule, FormsModule],
    exports: [RouterModule, CommonModule, FormsModule, ExtractArrayValue]
})
export class SharedModule {}