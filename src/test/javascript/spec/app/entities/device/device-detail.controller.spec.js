'use strict';

describe('Controller Tests', function() {

    describe('Device Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockDevice, MockBranch;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockDevice = jasmine.createSpy('MockDevice');
            MockBranch = jasmine.createSpy('MockBranch');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'Device': MockDevice,
                'Branch': MockBranch
            };
            createController = function() {
                $injector.get('$controller')("DeviceDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'customeranalyticsApp:deviceUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
