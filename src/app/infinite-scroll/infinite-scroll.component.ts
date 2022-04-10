import {AfterViewInit, Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';

// stolen from https://netbasal.com/build-an-infinite-scroll-component-in-angular-a9c16907a94d
@Component({
    selector: 'app-infinite-scroll',
    template: '<ng-content></ng-content><div #anchor>Loading more results</div>', // TODO: doesnt work if div is empty?
})
export class InfiniteScrollComponent implements OnInit, AfterViewInit {
    @Input() options = {};
    @Output() scrolled = new EventEmitter();
    @ViewChild('anchor') anchor!: ElementRef<HTMLElement>;

    private observer!: IntersectionObserver;

    constructor(private host: ElementRef) {
    }

    get element() {
        return this.host.nativeElement;
    }

    ngOnInit() {
        const options = {
            root: this.isHostScrollable() ? this.host.nativeElement : null,
            ...this.options
        };

        this.observer = new IntersectionObserver(([entry]) => {
            entry.isIntersecting && this.scrolled.emit();
        }, options);
    }

    ngAfterViewInit() {
        this.observer.observe(this.anchor.nativeElement);
    }

    private isHostScrollable() {
        const style = window.getComputedStyle(this.element);

        return style.getPropertyValue('overflow') === 'auto' ||
            style.getPropertyValue('overflow-y') === 'scroll';
    }

    ngOnDestroy() {
        this.observer.disconnect();
    }
}
